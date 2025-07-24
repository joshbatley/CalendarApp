import { Box } from "@muffled-ui/react"
import { EventBox } from "./EventBox";
import { DatePicker } from "./DatePicker";

type SearchAreaProps = {
  searchEvents: CalendarEvents[];
  onChange: (e: any) => void;
}

export const SearchArea: React.FC<SearchAreaProps> = ({ searchEvents, onChange }) => (
  <Box>
    <DatePicker onChange={onChange} />
    {searchEvents.length > 0 ? (
      <Box mt="4">
        <EventBox events={searchEvents} />
      </Box>
    ) : null}
  </Box>
);
